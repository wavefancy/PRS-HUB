<template>
  <div>
    <div class="d-flex flex-column flex-lg-row h-lg-full bg-surface-secondary">
      <SidebarItem></SidebarItem>
      <router-view></router-view>
    </div> 
  </div>
</template>

<script>
import SidebarItem from "@/components/commons/SidebarItem"
export default {
    name:"FunctionItem",
    components: {
        SidebarItem
    }
}
</script>

<style>

</style>