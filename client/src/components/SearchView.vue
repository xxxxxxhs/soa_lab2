<script setup>
import { ref } from 'vue'
import { countDistanceGreaterThan, getVehicle, maxEnginePower, typeLessThan } from '../api.js'
import { TYPES, formatNumber, plural, typeLabel } from '../labels.js'
import { clearNotice, showError } from '../notice.js'
import VehicleCard from './VehicleCard.vue'
import VehicleTable from './VehicleTable.vue'

const byId = ref('')
const found = ref(null)

const strongest = ref(undefined)

const distance = ref('')
const count = ref(null)

const lessThanType = ref('BOAT')
const lesser = ref(null)

async function attempt(action) {
  try {
    clearNotice()
    await action()
  } catch (error) {
    showError(error)
  }
}

function findById() {
  found.value = null
  if (byId.value.trim() === '') {
    showError(new Error('Введите идентификатор'))
    return
  }
  attempt(async () => {
    found.value = await getVehicle(byId.value.trim())
  })
}

function findStrongest() {
  attempt(async () => {
    strongest.value = await maxEnginePower()
  })
}

function countByDistance() {
  count.value = null
  if (distance.value.trim() === '') {
    showError(new Error('Введите значение пробега'))
    return
  }
  attempt(async () => {
    count.value = { value: await countDistanceGreaterThan(distance.value.trim()), distance: distance.value.trim() }
  })
}

function findLesserTypes() {
  attempt(async () => {
    lesser.value = { type: lessThanType.value, vehicles: await typeLessThan(lessThanType.value) }
  })
}
</script>

<template>
  <section class="panel">
    <h2>Поиск по идентификатору</h2>
    <form class="row" novalidate @submit.prevent="findById">
      <input v-model="byId" type="text" inputmode="numeric" placeholder="ID" class="short">
      <button type="submit">Найти</button>
    </form>
    <VehicleCard v-if="found" :vehicle="found" />
  </section>

  <section class="panel">
    <h2>Самый мощный двигатель</h2>
    <p class="hint">Одно (любое) транспортное средство с максимальной мощностью двигателя.</p>
    <button type="button" @click="findStrongest">Найти</button>
    <VehicleCard v-if="strongest" :vehicle="strongest" />
    <p v-else-if="strongest === null" class="empty">Коллекция пуста.</p>
  </section>

  <section class="panel">
    <h2>Пробег больше заданного</h2>
    <form class="row" novalidate @submit.prevent="countByDistance">
      <input v-model="distance" type="text" inputmode="decimal" placeholder="например, 1500.5" class="short">
      <button type="submit">Посчитать</button>
    </form>
    <p v-if="count" class="result">
      Пробег больше {{ formatNumber(count.distance) }} у {{ count.value }}
      {{ plural(count.value, 'транспортного средства', 'транспортных средств', 'транспортных средств') }}.
    </p>
  </section>

  <section class="panel">
    <h2>Тип меньше заданного</h2>
    <p class="hint">Порядок типов: Самолёт &lt; Вертолёт &lt; Лодка &lt; Корабль &lt; Велосипед.</p>
    <form class="row" novalidate @submit.prevent="findLesserTypes">
      <select v-model="lessThanType">
        <option v-for="type in TYPES" :key="type.value" :value="type.value">{{ type.label }}</option>
      </select>
      <button type="submit">Найти</button>
    </form>
    <template v-if="lesser">
      <VehicleTable v-if="lesser.vehicles.length" :vehicles="lesser.vehicles" />
      <p v-else class="empty">
        Транспортных средств с типом меньше «{{ typeLabel(lesser.type) }}» нет.
      </p>
    </template>
  </section>
</template>
