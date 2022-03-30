<template>
  <div>
    <router-view></router-view>
    <TipsItem></TipsItem>
  </div>
</template>
<script>
import TipsItem from '@/components/commons/TipsItem.vue'
  export default {
    name: 'App',
    components: {
      TipsItem
    }
  }
</script>
<style >
.el-button--primary {
    background-color: #796CFF !important;
    border-color: #796CFF !important;
}
</style>