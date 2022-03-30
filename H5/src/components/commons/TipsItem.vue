<template>
  <!-- 提示弹层 -->
  <div class="tips" v-show="tips.show">
    <h3>{{tips.title}}</h3> 
  </div>
</template>
<script>
  export default {
  name: 'TipsItem',
  data (){
    return {
      // 可以根据实际情况写更复杂的弹层，根据情况扩充tips即可
      tips:{
        show:false,
        title:'',
        time:1500
      }
    }
  },
  created () {
    this.$bus.$on('tips',(data)=>{
      this.tips = data;
    })
  },
  watch:{
    // 检测tips变化，显示提示1.5s之后自动关闭，可根据实际情况自动修改时间
    tips:function(){
      if(this.tips.show){
        setTimeout( ()=>{
          this.tips.show = false;
        },this.tips.time)
      }
    }
  }
}
</script>
<style scoped>
.tips{
  display: flex;
  justify-content: center;
  flex-direction: column;
  align-items: center;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 999;
  width: 100%;
  height: 100%;
  color: #fff;
  background-color: rgba(228,231,237,0.5);/*通过rgba方式设置背景颜色透明度*/
  
}
.tips h3{
    padding: .1rem .2rem;
    font-size: 18px;
    line-height: 18px;
    border-radius: 4px;
    color: rgb(253, 4, 4);
  }
</style>