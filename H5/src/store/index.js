import Vue from 'vue'
//引入Vuex
import Vuex from 'vuex'
//算法参数-提交后台用
import algorithmsData from '@/store/modules/algorithmsData'
import uploadFileData from '@/store/modules/uploadFileData'
import loginData from '@/store/modules/loginData'
//应用Vuex插件
Vue.use(Vuex)

//创建并暴露store
export default new Vuex.Store({
    modules:{
        algorithmsData,
        uploadFileData,
        loginData
    }

})