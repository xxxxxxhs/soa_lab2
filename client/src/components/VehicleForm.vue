<script setup>
import { reactive } from 'vue'
import { TYPES } from '../labels.js'

const props = defineProps({
  mode: { type: String, required: true },
  vehicle: { type: Object, default: null },
  submitLabel: { type: String, default: 'Сохранить' },
  busy: { type: Boolean, default: false }
})

const emit = defineEmits(['submit', 'cancel'])

const source = props.vehicle
const form = reactive({
  name: source?.name ?? '',
  x: source ? String(source.coordinates.x) : '',
  y: source ? String(source.coordinates.y) : '',
  enginePower: source ? String(source.enginePower) : '',
  capacity: source ? String(source.capacity) : '',
  distanceTravelled: source ? String(source.distanceTravelled) : '',
  type: source?.type ?? '',
  numberOfWheels: source ? String(source.numberOfWheels) : ''
})

const selected = reactive({
  name: false,
  coordinates: false,
  enginePower: false,
  capacity: false,
  distanceTravelled: false,
  type: false,
  numberOfWheels: false
})

const isPatch = props.mode === 'patch'

function touch(field) {
  if (isPatch) {
    selected[field] = true
  }
}

function submit() {
  emit('submit', { ...form }, { ...selected })
}
</script>

<template>
  <form class="vehicle-form" novalidate @submit.prevent="submit">
    <p v-if="isPatch" class="hint">Отметьте поля, которые нужно изменить. Остальные останутся прежними.</p>
    <p v-else class="hint">
      Обязательны все поля, кроме координаты X и числа колёс (по умолчанию 0).
      Данные проверяет сервис — при ошибке вы увидите его ответ.
    </p>

    <div class="form-grid">
      <label class="field wide">
        <span><input v-if="isPatch" v-model="selected.name" type="checkbox"> Название</span>
        <input v-model="form.name" type="text" placeholder="Aurora" @input="touch('name')">
      </label>

      <fieldset class="field wide coordinates">
        <legend><input v-if="isPatch" v-model="selected.coordinates" type="checkbox"> Координаты</legend>
        <label>
          <span>X</span>
          <input v-model="form.x" type="text" inputmode="numeric" placeholder="0" @input="touch('coordinates')">
        </label>
        <label>
          <span>Y (не меньше −942)</span>
          <input v-model="form.y" type="text" inputmode="numeric" placeholder="20" @input="touch('coordinates')">
        </label>
      </fieldset>

      <label class="field">
        <span><input v-if="isPatch" v-model="selected.enginePower" type="checkbox"> Мощность двигателя</span>
        <input v-model="form.enginePower" type="text" inputmode="numeric" placeholder="больше 0"
               @input="touch('enginePower')">
      </label>

      <label class="field">
        <span><input v-if="isPatch" v-model="selected.capacity" type="checkbox"> Вместимость</span>
        <input v-model="form.capacity" type="text" inputmode="decimal" placeholder="больше 0"
               @input="touch('capacity')">
      </label>

      <label class="field">
        <span><input v-if="isPatch" v-model="selected.distanceTravelled" type="checkbox"> Пробег</span>
        <input v-model="form.distanceTravelled" type="text" inputmode="decimal" placeholder="0 или больше"
               @input="touch('distanceTravelled')">
      </label>

      <label class="field">
        <span><input v-if="isPatch" v-model="selected.type" type="checkbox"> Тип</span>
        <select v-model="form.type" @change="touch('type')">
          <option value="">— не выбран —</option>
          <option v-for="type in TYPES" :key="type.value" :value="type.value">{{ type.label }}</option>
        </select>
      </label>

      <label class="field">
        <span><input v-if="isPatch" v-model="selected.numberOfWheels" type="checkbox"> Число колёс</span>
        <input v-model="form.numberOfWheels" type="text" inputmode="numeric" placeholder="0"
               @input="touch('numberOfWheels')">
      </label>
    </div>

    <div class="actions">
      <button type="submit" :disabled="busy">{{ submitLabel }}</button>
      <button v-if="mode !== 'create'" type="button" class="secondary" @click="emit('cancel')">Отмена</button>
    </div>
  </form>
</template>
