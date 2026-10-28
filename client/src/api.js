export const endpoints = {
  vehicles: import.meta.env.VITE_VEHICLES_API || `${location.protocol}//${location.hostname}:8443/api/v1`,
  shop: import.meta.env.VITE_SHOP_API || '/api/v1/shop'
}

export const SERVICES = {
  vehicles: 'Сервис коллекции',
  shop: 'Сервис автомастерской'
}

export class ApiError extends Error {
  constructor(service, status, message) {
    super(message)
    this.service = service
    this.status = status
  }
}

function parseXml(service, text) {
  const doc = new DOMParser().parseFromString(text, 'application/xml')
  if (doc.getElementsByTagName('parsererror').length > 0) {
    throw new ApiError(service, 0, 'Ответ сервиса не является корректным XML')
  }
  return doc.documentElement
}

function child(element, name) {
  for (const node of element.children) {
    if (node.localName === name) {
      return node
    }
  }
  return null
}

function childText(element, name) {
  const node = child(element, name)
  return node ? node.textContent : null
}

function children(element, name) {
  return Array.from(element.children).filter(node => node.localName === name)
}

async function send(service, url, { method = 'GET', body } = {}) {
  const headers = { Accept: 'application/xml' }
  if (body !== undefined) {
    headers['Content-Type'] = 'application/xml'
  }
  let response
  try {
    response = await fetch(url, { method, headers, body })
  } catch {
    throw new ApiError(service, 0,
      'Не удалось связаться с сервисом. Проверьте, что он запущен и что браузер доверяет его сертификату.')
  }
  if (response.status === 204) {
    return { response, root: null }
  }
  const text = await response.text()
  if (!response.ok) {
    let message = `HTTP ${response.status}`
    try {
      const root = parseXml(service, text)
      if (root.localName === 'error') {
        message = childText(root, 'message') || message
      }
    } catch {
      message = `HTTP ${response.status}`
    }
    throw new ApiError(service, response.status, message)
  }
  return { response, root: text ? parseXml(service, text) : null }
}

function parseVehicle(element) {
  const coordinates = child(element, 'coordinates')
  return {
    id: Number(childText(element, 'id')),
    name: childText(element, 'name'),
    coordinates: {
      x: Number(childText(coordinates, 'x')),
      y: Number(childText(coordinates, 'y'))
    },
    creationDate: childText(element, 'creationDate'),
    enginePower: Number(childText(element, 'enginePower')),
    capacity: Number(childText(element, 'capacity')),
    distanceTravelled: Number(childText(element, 'distanceTravelled')),
    type: childText(element, 'type'),
    numberOfWheels: Number(childText(element, 'numberOfWheels'))
  }
}

function escapeXml(value) {
  return String(value)
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&apos;')
}

function element(name, value) {
  return `<${name}>${escapeXml(value)}</${name}>`
}

const SIMPLE_FIELDS = ['enginePower', 'capacity', 'distanceTravelled', 'type', 'numberOfWheels']

export function vehicleXml(form, include) {
  const parts = []
  if (include('name')) {
    parts.push(element('name', form.name))
  }
  if (include('coordinates')) {
    const coordinates = []
    if (form.x.trim() !== '') {
      coordinates.push(element('x', form.x.trim()))
    }
    if (form.y.trim() !== '') {
      coordinates.push(element('y', form.y.trim()))
    }
    parts.push(`<coordinates>${coordinates.join('')}</coordinates>`)
  }
  for (const field of SIMPLE_FIELDS) {
    if (include(field)) {
      parts.push(element(field, form[field].trim()))
    }
  }
  return `<?xml version="1.0" encoding="UTF-8"?><vehicle>${parts.join('')}</vehicle>`
}

function filledFields(form) {
  return field => field === 'coordinates'
    ? form.x.trim() !== '' || form.y.trim() !== ''
    : form[field].trim() !== ''
}

function vehicleUrl(id) {
  return `${endpoints.vehicles}/vehicles/${encodeURIComponent(id)}`
}

export async function listVehicles({ sorts, filters, page, size }) {
  const params = new URLSearchParams()
  if (sorts.length > 0) {
    params.set('sort', sorts.map(sort => (sort.descending ? '-' : '') + sort.field).join(','))
  }
  for (const filter of filters) {
    params.append('filter', `${filter.field}[${filter.operator}]=${filter.value}`)
  }
  if (String(page).trim() !== '') {
    params.set('page', String(page).trim())
  }
  if (String(size).trim() !== '') {
    params.set('size', String(size).trim())
  }
  const query = params.toString()
  const { root } = await send('vehicles', `${endpoints.vehicles}/vehicles${query ? `?${query}` : ''}`)
  if (!root) {
    return null
  }
  return {
    vehicles: children(child(root, 'vehicles'), 'vehicle').map(parseVehicle),
    page: Number(childText(root, 'page')),
    size: Number(childText(root, 'size')),
    totalElements: Number(childText(root, 'totalElements')),
    totalPages: Number(childText(root, 'totalPages'))
  }
}

export async function getVehicle(id) {
  const { root } = await send('vehicles', vehicleUrl(id))
  return parseVehicle(root)
}

export async function createVehicle(form) {
  const { response, root } = await send('vehicles', `${endpoints.vehicles}/vehicles`, {
    method: 'POST',
    body: vehicleXml(form, filledFields(form))
  })
  return { vehicle: parseVehicle(root), location: response.headers.get('Location') }
}

export async function replaceVehicle(id, form) {
  const { root } = await send('vehicles', vehicleUrl(id), {
    method: 'PUT',
    body: vehicleXml(form, filledFields(form))
  })
  return parseVehicle(root)
}

export async function patchVehicle(id, form, selected) {
  const { root } = await send('vehicles', vehicleUrl(id), {
    method: 'PATCH',
    body: vehicleXml(form, field => selected[field])
  })
  return parseVehicle(root)
}

export async function deleteVehicle(id) {
  await send('vehicles', vehicleUrl(id), { method: 'DELETE' })
}

export async function maxEnginePower() {
  const { root } = await send('vehicles', `${endpoints.vehicles}/vehicles/max-engine-power`)
  return root ? parseVehicle(root) : null
}

export async function countDistanceGreaterThan(distance) {
  const { root } = await send('vehicles',
    `${endpoints.vehicles}/vehicles/distance-travelled/greater-than/${encodeURIComponent(distance)}/count`)
  return Number(childText(root, 'count'))
}

export async function typeLessThan(type) {
  const { root } = await send('vehicles',
    `${endpoints.vehicles}/vehicles/type/less-than/${encodeURIComponent(type)}`)
  return root ? children(root, 'vehicle').map(parseVehicle) : []
}

export async function fixDistance(vehicleId) {
  const { response, root } = await send('shop',
    `${endpoints.shop}/fix-distance/${encodeURIComponent(vehicleId)}`, { method: 'PATCH' })
  return { vehicle: parseVehicle(root), location: response.url }
}

export async function addWheels(vehicleId, numberOfWheels) {
  const { response, root } = await send('shop',
    `${endpoints.shop}/add-wheels/${encodeURIComponent(vehicleId)}/${encodeURIComponent(numberOfWheels)}`,
    { method: 'PATCH' })
  return { vehicle: parseVehicle(root), location: response.url }
}
