import requests from "../utils/requests";

export default {
    //注册
    auth(params){return requests.get(`/auth`,params);},
    //登录
    reqLogin(params){return requests.get(`/login`,params);},
    //初始化用户数据
    getUserInfo(params){return requests.get(`/getUserInfo`,params);}
}