import requests from "../utils/requests";

export default {
    //提交参数
    setParametersInfo(params){return requests.post(`/algorithms/setParametersInfo`,params);},
    //获取算法数据
    getAlgorithmsInfo(params){return requests.get(`/algorithms/getAlgorithmsInfo`,params);},
    //获取上传文件信息
    getFileList(params){return requests.get(`/getFileList`,params);},
    //删除文件
    deleteFile(params){return requests.get(`/deleteFile`,params);},
    //延长文件有效时间
    extensionFileValidTime(params){return requests.get(`/extensionFileValidTime`,params);},
    //获取运行统计结果
    getRunnerStatis(params){return requests.get(`/getRunnerStatis`,params);},
    
}