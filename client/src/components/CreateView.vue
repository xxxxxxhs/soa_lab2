<script setup>
import { ref } from 'vue'
import { createVehicle } from '../api.js'
import { clearNotice, showError, showSuccess } from '../notice.js'
import VehicleCard from './VehicleCard.vue'
import VehicleForm from './VehicleForm.vue'

const busy = ref(false)
const created = ref(null)

async function onCreate(form) {
  busy.value = true
  clearNotice()
  try {
    created.value = await createVehicle(form)
    showSuccess(`Транспортное средство создано, ID ${created.value.vehicle.id}.`)
  } catch (error) {
    showError(error)
  } finally {
    busy.value = false
  }
}
</script>

<template>
  <section class="panel">
    <h2>Новое транспортное средство</h2>
    <VehicleForm mode="create" submit-label="Добавить" :busy="busy" @submit="onCreate" />
  </section>

  <section v-if="created" class="panel">
    <VehicleCard :vehicle="created.vehicle" title="Добавлено" />
    <p class="hint">Адрес нового ресурса: <code>{{ created.location }}</code></p>
  </section>
</template>
