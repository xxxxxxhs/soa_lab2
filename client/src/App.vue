<script setup>
import { ref } from 'vue'
import { endpoints } from './api.js'
import { clearNotice } from './notice.js'
import CollectionView from './components/CollectionView.vue'
import CreateView from './components/CreateView.vue'
import NoticeBar from './components/NoticeBar.vue'
import SearchView from './components/SearchView.vue'
import ShopView from './components/ShopView.vue'

const TABS = [
  { key: 'collection', label: 'Коллекция' },
  { key: 'create', label: 'Добавить' },
  { key: 'search', label: 'Поиск и статистика' },
  { key: 'shop', label: 'Автомастерская' }
]

const tab = ref('collection')

function open(key) {
  tab.value = key
  clearNotice()
}
</script>

<template>
  <header class="header">
    <div class="container">
      <h1>Автопарк</h1>
      <nav class="tabs">
        <button
          v-for="item in TABS"
          :key="item.key"
          type="button"
          :class="{ active: tab === item.key }"
          @click="open(item.key)"
        >
          {{ item.label }}
        </button>
      </nav>
    </div>
  </header>

  <main class="container">
    <NoticeBar />
    <CollectionView v-if="tab === 'collection'" />
    <CreateView v-else-if="tab === 'create'" />
    <SearchView v-else-if="tab === 'search'" />
    <ShopView v-else />
  </main>

  <footer class="container footer">
    Сервис коллекции: <a :href="`${endpoints.vehicles}/vehicles`" target="_blank" rel="noopener">{{ endpoints.vehicles }}</a>
    · Сервис автомастерской: <code>{{ endpoints.shop }}</code>
  </footer>
</template>
