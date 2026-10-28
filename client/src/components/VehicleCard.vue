<script setup>
import { formatDate, formatNumber, typeLabel } from '../labels.js'

defineProps({
  vehicle: { type: Object, required: true },
  title: { type: String, default: '' }
})
</script>

<template>
  <article class="vehicle-card">
    <h3 v-if="title">{{ title }}</h3>
    <p>
      <strong>«{{ vehicle.name }}»</strong> — {{ typeLabel(vehicle.type).toLowerCase() }} с идентификатором {{ vehicle.id }},
      добавлен в коллекцию {{ formatDate(vehicle.creationDate) }}.
      Мощность двигателя — {{ formatNumber(vehicle.enginePower) }}, вместимость — {{ formatNumber(vehicle.capacity) }},
      пробег — {{ formatNumber(vehicle.distanceTravelled) }}, колёс — {{ vehicle.numberOfWheels }}.
      Находится в точке с координатами ({{ vehicle.coordinates.x }}; {{ vehicle.coordinates.y }}).
    </p>
  </article>
</template>
