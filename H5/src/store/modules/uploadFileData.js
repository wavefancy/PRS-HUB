//文件上传状态信息
export default {
    namespaced: true,
    actions: {
        setUploadFlag (context,val) {
            context.commit("SET_UPLOAD_FLAG",val)
        }
    },
    mutations: {
        SET_UPLOAD_FLAG(state,val){
            state.fileData=val
        }
    },
    state: {
        fileData:{
            uploadFlag:false,
            fileId:null
        }
    },
    getters: {
        getFileData(state){
            return state.fileData
        }
    }
}