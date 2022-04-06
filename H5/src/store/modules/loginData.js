//登录信息
export default {
    namespaced: true,
    actions: {
        setLoginData (context,val) {
            context.commit("SET_LOGIN_DATA",val)
        }
    },
    mutations: {
        SET_LOGIN_DATA(state,val){
            state.loginData=val
        }
    },
    state: {
        loginData:{
            accessToken:''
        }
    },
    getters: {
        getLoginData(state){
            return state.loginData
        }
    }
}