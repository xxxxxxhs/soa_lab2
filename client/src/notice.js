import { ref } from 'vue'
import { ApiError, SERVICES } from './api.js'
import { STATUS_TEXT } from './labels.js'

export const notice = ref(null)

export function showSuccess(text) {
  notice.value = { kind: 'success', title: 'Готово', text }
}

export function showInfo(text) {
  notice.value = { kind: 'info', title: 'Результат', text }
}

export function showError(error) {
  if (error instanceof ApiError) {
    const status = error.status ? `${error.status} — ${STATUS_TEXT[error.status] ?? 'ошибка'}` : STATUS_TEXT[0]
    notice.value = { kind: 'error', title: `${SERVICES[error.service]}: ${status}`, text: error.message }
  } else {
    notice.value = { kind: 'error', title: 'Ошибка', text: String(error?.message ?? error) }
  }
}

export function clearNotice() {
  notice.value = null
}
