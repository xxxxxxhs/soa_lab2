<script setup>
import { onMounted, reactive, ref } from 'vue'
import {
  addWheels, deleteVehicle, fixDistance, getVehicle, listVehicles, patchVehicle, replaceVehicle
} from '../api.js'
import { FIELDS, OPERATORS, TYPES, plural } from '../labels.js'
import { clearNotice, showError, showInfo, showSuccess } from '../notice.js'
import VehicleCard from './VehicleCard.vue'
import VehicleForm from './VehicleForm.vue'
import VehicleTable from './VehicleTable.vue'

const query = reactive({
  sorts: [],
  filters: [],
  page: '1',
  size: '10'
})

const result = ref(null)
const loaded = ref(false)
const busy = ref(false)
const selected = ref(null)
const editMode = ref(null)
const wheels = ref('1')

function addSort() {
  query.sorts.push({ field: 'id', descending: false })
}

function addFilter() {
  query.filters.push({ field: 'name', operator: 'eq', value: '' })
}

function onFilterFieldChange(filter) {
  if (filter.field === 'type' && !TYPES.some(type => type.value === filter.value)) {
    filter.value = TYPES[0].value
  }
}

async function load() {
  busy.value = true
  try {
    result.value = await listVehicles(query)
    loaded.value = true
    if (result.value === null) {
      showInfo('По заданным условиям ничего не найдено (ответ 204 No Content).')
    }
  } catch (error) {
    showError(error)
  } finally {
    busy.value = false
  }
}

function reload() {
  clearNotice()
  load()
}

function search() {
  query.page = '1'
  reload()
}

function resetQuery() {
  query.sorts = []
  query.filters = []
  query.page = '1'
  query.size = '10'
  reload()
}

function goToPage(page) {
  query.page = String(page)
  reload()
}

function select(vehicle) {
  selected.value = vehicle
  editMode.value = null
}

async function refreshSelected() {
  if (!selected.value) {
    return
  }
  try {
    selected.value = await getVehicle(selected.value.id)
  } catch (error) {
    selected.value = null
    showError(error)
  }
}

async function run(action, message) {
  busy.value = true
  clearNotice()
  try {
    const vehicle = await action()
    if (vehicle) {
      selected.value = vehicle
    }
    editMode.value = null
    showSuccess(message)
    await load()
  } catch (error) {
    showError(error)
  } finally {
    busy.value = false
  }
}

function onReplace(form) {
  run(() => replaceVehicle(selected.value.id, form), `Транспортное средство ${selected.value.id} обновлено целиком.`)
}

function onPatch(form, fields) {
  run(() => patchVehicle(selected.value.id, form, fields), `Транспортное средство ${selected.value.id} обновлено частично.`)
}

async function onDelete() {
  const id = selected.value.id
  await run(async () => {
    await deleteVehicle(id)
    selected.value = null
    return null
  }, `Транспортное средство ${id} удалено.`)
}

function onFixDistance() {
  run(async () => (await fixDistance(selected.value.id)).vehicle,
    `Автомастерская обнулила пробег транспортного средства ${selected.value.id}.`)
}

function onAddWheels() {
  const count = wheels.value
  run(async () => (await addWheels(selected.value.id, count)).vehicle,
    `Автомастерская добавила колёса транспортному средству ${selected.value.id}.`)
}

onMounted(load)
</script>

<template>
  <section class="panel">
    <h2>Параметры выборки</h2>

    <div class="query-grid">
      <div>
        <h3>Сортировка</h3>
        <p v-if="query.sorts.length === 0" class="hint">Без сортировки элементы идут по возрастанию ID.</p>
        <div v-for="(sort, index) in query.sorts" :key="index" class="row">
          <span class="order">{{ index + 1 }}</span>
          <select v-model="sort.field" aria-label="Поле сортировки">
            <option v-for="field in FIELDS" :key="field.key" :value="field.key">{{ field.label }}</option>
          </select>
          <select v-model="sort.descending" aria-label="Направление">
            <option :value="false">по возрастанию</option>
            <option :value="true">по убыванию</option>
          </select>
          <button type="button" class="ghost" aria-label="Убрать" @click="query.sorts.splice(index, 1)">✕</button>
        </div>
        <button type="button" class="secondary small" @click="addSort">+ поле сортировки</button>
      </div>

      <div>
        <h3>Фильтры <span class="hint">(все условия должны выполняться)</span></h3>
        <p v-if="query.filters.length === 0" class="hint">Фильтры не заданы.</p>
        <div v-for="(filter, index) in query.filters" :key="index" class="row">
          <select v-model="filter.field" aria-label="Поле" @change="onFilterFieldChange(filter)">
            <option v-for="field in FIELDS" :key="field.key" :value="field.key">{{ field.label }}</option>
          </select>
          <select v-model="filter.operator" class="operator" aria-label="Оператор">
            <option v-for="operator in OPERATORS" :key="operator.value" :value="operator.value">
              {{ operator.label }}
            </option>
          </select>
          <select v-if="filter.field === 'type'" v-model="filter.value" aria-label="Значение">
            <option v-for="type in TYPES" :key="type.value" :value="type.value">{{ type.label }}</option>
          </select>
          <input
            v-else
            v-model="filter.value"
            type="text"
            aria-label="Значение"
            :placeholder="filter.field === 'creationDate' ? '2026-10-22T16:00:00+03:00' : 'значение'"
          >
          <button type="button" class="ghost" aria-label="Убрать" @click="query.filters.splice(index, 1)">✕</button>
        </div>
        <button type="button" class="secondary small" @click="addFilter">+ условие</button>
      </div>
    </div>

    <div class="row toolbar">
      <label class="inline">Размер страницы <input v-model="query.size" type="text" inputmode="numeric" class="short"></label>
      <button type="button" :disabled="busy" @click="search">Показать</button>
      <button type="button" class="secondary" :disabled="busy" @click="resetQuery">Сбросить</button>
    </div>
  </section>

  <section class="panel">
    <div class="panel-head">
      <h2>Транспортные средства</h2>
      <p v-if="result" class="hint">
        Найдено {{ result.totalElements }}
        {{ plural(result.totalElements, 'транспортное средство', 'транспортных средства', 'транспортных средств') }},
        страница {{ result.page }} из {{ result.totalPages }}
      </p>
    </div>

    <p v-if="loaded && !result" class="empty">Ничего не найдено: коллекция пуста, под фильтры ничего не подошло или страница вне выборки.</p>
    <VehicleTable
      v-if="result"
      :vehicles="result.vehicles"
      :selected-id="selected?.id ?? null"
      selectable
      @select="select"
    />
    <p v-if="result" class="hint">Нажмите на строку, чтобы выбрать транспортное средство.</p>

    <div class="row pagination">
      <button type="button" class="secondary" :disabled="busy || !result || result.page <= 1" @click="goToPage(result.page - 1)">
        ← Назад
      </button>
      <label class="inline">Страница <input v-model="query.page" type="text" inputmode="numeric" class="short" @keyup.enter="reload"></label>
      <button type="button" class="secondary" :disabled="busy" @click="reload">Перейти</button>
      <button type="button" class="secondary" :disabled="busy || !result || result.page >= result.totalPages" @click="goToPage(result.page + 1)">
        Вперёд →
      </button>
    </div>
  </section>

  <section v-if="selected" class="panel">
    <div class="panel-head">
      <h2>Выбрано: ID {{ selected.id }}</h2>
      <button type="button" class="ghost" @click="clearNotice(); refreshSelected()">Обновить</button>
    </div>
    <VehicleCard :vehicle="selected" />

    <div v-if="!editMode" class="actions wrap">
      <button type="button" @click="editMode = 'replace'">Изменить целиком</button>
      <button type="button" @click="editMode = 'patch'">Изменить частично</button>
      <button type="button" class="danger" :disabled="busy" @click="onDelete">Удалить</button>
      <span class="divider" />
      <button type="button" class="secondary" :disabled="busy" @click="onFixDistance">Скрутить пробег</button>
      <input v-model="wheels" type="text" inputmode="numeric" class="short" aria-label="Сколько колёс добавить">
      <button type="button" class="secondary" :disabled="busy" @click="onAddWheels">Добавить колёса</button>
    </div>

    <VehicleForm
      v-if="editMode === 'replace'"
      :key="`replace-${selected.id}`"
      mode="replace"
      :vehicle="selected"
      submit-label="Сохранить (PUT)"
      :busy="busy"
      @submit="onReplace"
      @cancel="editMode = null"
    />
    <VehicleForm
      v-if="editMode === 'patch'"
      :key="`patch-${selected.id}`"
      mode="patch"
      :vehicle="selected"
      submit-label="Сохранить (PATCH)"
      :busy="busy"
      @submit="onPatch"
      @cancel="editMode = null"
    />
  </section>
</template>
