import Vue from 'vue'
import App from './App.vue'
//引入VueRouter插件
import VueRouter from 'vue-router'
//引入store
import store from './store'
//引入路由器router
import router from './router'
//引入lodaing组件
import VueElementLoading from 'vue-element-loading'
import {Pagination,Message , MessageBox,Tooltip  } from 'element-ui'
//引入element-ui全局样式
import 'element-ui/lib/theme-chalk/index.css'
//
import VModal from 'vue-js-modal'

//上传组件
import uploader from 'vue-simple-uploader'

Vue.use(uploader)

Vue.use(VModal, {dialog: true, dynamic: true})

//导入bootstrap
// import 'bootstrap/dist/css/bootstrap.css'

//应用ElementUI
Vue.component('VueElementLoading', VueElementLoading)
//ElementUI注册组件的时候，还有一种写法，挂在原型上
Vue.prototype.$message = Message;
Vue.prototype.$MessageBox = MessageBox;
Vue.use(Pagination);
Vue.use(Tooltip)
Vue.use(VueRouter)

Vue.config.productionTip = false

new Vue({
  render: h => h(App),
  store,
  router,
  beforeCreate() {
    //定义全局事件总线
    Vue.prototype.$bus = this
  },
}).$mount('#app')
