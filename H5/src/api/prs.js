import requests from "../utils/requests";

export default {
    //提交参数
    setParametersInfo(params){return requests.post(`/algorithms/setParametersInfo`,params);},
    //获取算法数据
    getAlgorithmsInfo(params){return requests.get(`/algorithms/getAlgorithmsInfo`,params);},
    //保存上传文件信息
    savePrsFileInfo(params){return requests.post(`/savePrsFileInfo`,params);},
    //获取上传文件信息
    getFileList(params){return requests.get(`/getFileList`,params);},
    //删除文件
    deleteFile(params){return requests.get(`/deleteFile`,params);},
    //延长文件有效时间
    extensionFileValidTime(params){return requests.get(`/extensionFileValidTime`,params);},
    //获取运行统计结果
    getRunnerStatis(params){return requests.get(`/getRunnerStatis`,params);},
    //下载运行结果
    downloadResult(params){return requests.get(`/downloadResult`,params);},
    //删除运行数据
    deleteRunner(params){return requests.get(`/deleteRunner`,params);},
    //中止运行
    abortRunner(params){return requests.get(`/abortRunner`,params);},
    //校验文件内容的title信息
    checkFileTitle(params){return requests.post(`/bigfile/checkFileTitle`,params);},
    
}