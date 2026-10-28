export const FIELDS = [
  { key: 'id', label: 'ID' },
  { key: 'name', label: 'Название' },
  { key: 'coordinates.x', label: 'Координата X' },
  { key: 'coordinates.y', label: 'Координата Y' },
  { key: 'creationDate', label: 'Дата создания' },
  { key: 'enginePower', label: 'Мощность двигателя' },
  { key: 'capacity', label: 'Вместимость' },
  { key: 'distanceTravelled', label: 'Пробег' },
  { key: 'type', label: 'Тип' },
  { key: 'numberOfWheels', label: 'Число колёс' }
]

export const TYPES = [
  { value: 'PLANE', label: 'Самолёт' },
  { value: 'HELICOPTER', label: 'Вертолёт' },
  { value: 'BOAT', label: 'Лодка' },
  { value: 'SHIP', label: 'Корабль' },
  { value: 'BICYCLE', label: 'Велосипед' }
]

export const OPERATORS = [
  { value: 'eq', label: '=' },
  { value: 'ne', label: '≠' },
  { value: 'gt', label: '>' },
  { value: 'gte', label: '≥' },
  { value: 'lt', label: '<' },
  { value: 'lte', label: '≤' }
]

export const STATUS_TEXT = {
  0: 'нет связи',
  400: 'некорректный запрос',
  404: 'не найдено',
  405: 'метод не поддерживается',
  406: 'неподдерживаемый формат ответа',
  415: 'неподдерживаемый формат данных',
  422: 'недопустимые данные',
  500: 'внутренняя ошибка сервиса',
  502: 'ошибка сервиса коллекции',
  503: 'сервис недоступен'
}

export function fieldLabel(key) {
  return FIELDS.find(field => field.key === key)?.label ?? key
}

export function typeLabel(value) {
  return TYPES.find(type => type.value === value)?.label ?? value
}

export function formatDate(value) {
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? value : date.toLocaleString('ru-RU')
}

export function formatNumber(value) {
  return Number(value).toLocaleString('ru-RU', { maximumFractionDigits: 6 })
}

export function plural(count, one, few, many) {
  const mod10 = count % 10
  const mod100 = count % 100
  if (mod10 === 1 && mod100 !== 11) {
    return one
  }
  if (mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14)) {
    return few
  }
  return many
}
