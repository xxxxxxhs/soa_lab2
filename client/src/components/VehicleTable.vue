<script setup>
import { formatDate, formatNumber, typeLabel } from '../labels.js'

defineProps({
  vehicles: { type: Array, required: true },
  selectedId: { type: Number, default: null },
  selectable: { type: Boolean, default: false }
})

const emit = defineEmits(['select'])
</script>

<template>
  <div class="table-wrap">
    <table>
      <thead>
        <tr>
          <th>ID</th>
          <th>Название</th>
          <th>Координаты</th>
          <th>Создано</th>
          <th class="num">Мощность</th>
          <th class="num">Вместимость</th>
          <th class="num">Пробег</th>
          <th>Тип</th>
          <th class="num">Колёс</th>
        </tr>
      </thead>
      <tbody>
        <tr
          v-for="vehicle in vehicles"
          :key="vehicle.id"
          :class="{ selectable, selected: vehicle.id === selectedId }"
          @click="selectable && emit('select', vehicle)"
        >
          <td>{{ vehicle.id }}</td>
          <td>{{ vehicle.name }}</td>
          <td>({{ vehicle.coordinates.x }}; {{ vehicle.coordinates.y }})</td>
          <td>{{ formatDate(vehicle.creationDate) }}</td>
          <td class="num">{{ formatNumber(vehicle.enginePower) }}</td>
          <td class="num">{{ formatNumber(vehicle.capacity) }}</td>
          <td class="num">{{ formatNumber(vehicle.distanceTravelled) }}</td>
          <td>{{ typeLabel(vehicle.type) }}</td>
          <td class="num">{{ vehicle.numberOfWheels }}</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>
