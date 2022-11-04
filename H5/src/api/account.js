import requests from "../utils/requests";

export default {
    //注册
    auth(params){return requests.get(`/auth`,params);},
    //登录
    reqLogin(params){return requests.get(`/login`,params);},
    //初始化用户数据
    getUserInfo(params){return requests.get(`/getUserInfo`,params);},
    //修改用户数据
    updatedUser(params){return requests.get(`/updatedUser`,params);},
    //删除用户数据
    deleteUser(params){return requests.get(`/deleteUser`,params);},
    //home页统计数据
    homeDatas(params){return requests.get(`/homeDatas`,params);}
}