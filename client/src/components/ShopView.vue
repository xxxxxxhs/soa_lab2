<script setup>
import { ref } from 'vue'
import { addWheels, fixDistance } from '../api.js'
import { clearNotice, showError, showSuccess } from '../notice.js'
import VehicleCard from './VehicleCard.vue'

const fixId = ref('')
const wheelsId = ref('')
const wheelsCount = ref('')
const result = ref(null)
const busy = ref(false)

async function attempt(action, message) {
  busy.value = true
  result.value = null
  clearNotice()
  try {
    result.value = await action()
    showSuccess(message)
  } catch (error) {
    showError(error)
  } finally {
    busy.value = false
  }
}

function onFixDistance() {
  attempt(() => fixDistance(fixId.value.trim()), 'Пробег обнулён.')
}

function onAddWheels() {
  attempt(() => addWheels(wheelsId.value.trim(), wheelsCount.value.trim()), 'Колёса добавлены.')
}
</script>

<template>
  <section class="panel">
    <h2>Скрутить пробег</h2>
    <p class="hint">Устанавливает пробег транспортного средства равным нулю.</p>
    <form class="row" novalidate @submit.prevent="onFixDistance">
      <input v-model="fixId" type="text" inputmode="numeric" placeholder="ID" class="short">
      <button type="submit" :disabled="busy">Скрутить</button>
    </form>
  </section>

  <section class="panel">
    <h2>Добавить колёса</h2>
    <p class="hint">Увеличивает число колёс транспортного средства на заданное значение.</p>
    <form class="row" novalidate @submit.prevent="onAddWheels">
      <input v-model="wheelsId" type="text" inputmode="numeric" placeholder="ID" class="short">
      <input v-model="wheelsCount" type="text" inputmode="numeric" placeholder="сколько колёс" class="short">
      <button type="submit" :disabled="busy">Добавить</button>
    </form>
  </section>

  <section v-if="result" class="panel">
    <VehicleCard :vehicle="result.vehicle" title="Состояние после операции" />
    <p class="hint">Автомастерская перенаправила на ресурс <code>{{ result.location }}</code>.</p>
  </section>
</template>
