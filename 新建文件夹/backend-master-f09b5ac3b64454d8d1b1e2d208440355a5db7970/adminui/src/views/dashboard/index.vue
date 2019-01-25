<template>
  <div style="text-align:center;padding-top:150px;">
    <img :src="logo" />
  </div>
</template>

<script>
import logo from '@/assets/images/logo.png'
export default {
  data() {
    return {
      logo
    }
  }
}
</script>
