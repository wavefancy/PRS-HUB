import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import { isEmpty }  from "@/utils/validate"

axios.defaults.timeout = 40000
axios.defaults.baseURL = process.env.VUE_APP_BASE_PRS_EPORTAL
axios.defaults.headers.post['Content-Type'] = 'application/json charset=UTF-8'

//请求拦截器：携带的token字段
axios.interceptors.request.use(
  config => {
    // do something before request is sent
    
    if (localStorage.getItem("accessToken")) {
      // 添加登录成功的token
      config.headers['accessToken'] = localStorage.getItem("accessToken")
      store.dispatch('loginData/setLoginData',{accessToken:localStorage.getItem("accessToken")})
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

//响应拦截器
axios.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    const res = response.data
    const code = res.code
    if(code==="400"){
      //清空accessToken
      localStorage.setItem('accessToken',"")
      return res
    }
    return res
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default {
  get(url, params = {}) {
      return axios.get(url, {
          params: params
      })
  },
  post(url, data = {},contentType) {
      if(!isEmpty(contentType)){
        axios.defaults.headers.post['Content-Type'] = contentType
      }
      return axios.post(url, data)
  }
}