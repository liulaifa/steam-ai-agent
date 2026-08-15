<template>
  <div v-if="visible" class="notification" :class="type">
    <span>{{ message }}</span>
  </div>
</template>

<script>
import { watch } from 'vue'

export default {
  name: 'AppNotification',
  props: {
    visible: {
      type: Boolean,
      default: false
    },
    message: {
      type: String,
      default: ''
    },
    type: {
      type: String,
      default: 'success',
      validator: (value) => ['success', 'error'].includes(value)
    },
    duration: {
      type: Number,
      default: 2000
    }
  },
  setup(props, { emit }) {
    watch(() => props.visible, (newVal) => {
      if (newVal) {
        setTimeout(() => {
          emit('close')
        }, props.duration)
      }
    })

    return {}
  }
}
</script>

<style scoped>
.notification {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  padding: 12px 24px;
  border-radius: 4px;
  color: white;
  font-size: 14px;
  z-index: 1000;
  animation: slideDown 0.3s ease-in-out;
}

.success {
  background-color: #4caf50;
  box-shadow: 0 2px 8px rgba(76, 175, 80, 0.3);
}

.error {
  background-color: #f44336;
  box-shadow: 0 2px 8px rgba(244, 67, 54, 0.3);
}

@keyframes slideDown {
  from {
    top: -50px;
    opacity: 0;
  }
  to {
    top: 20px;
    opacity: 1;
  }
}
</style>