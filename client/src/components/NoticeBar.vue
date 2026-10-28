<script setup>
import { clearNotice, notice } from '../notice.js'
</script>

<template>
  <div v-if="notice" class="notice" :class="`notice-${notice.kind}`" role="alert">
    <div>
      <strong>{{ notice.title }}</strong>
      <p>{{ notice.text }}</p>
    </div>
    <button type="button" class="ghost" aria-label="Закрыть" @click="clearNotice">✕</button>
  </div>
</template>
