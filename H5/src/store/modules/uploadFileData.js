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
            state.fileData.uploadFlag=val
        }
    },
    state: {
        fileData:{
            uploadFlag:false
        }
    },
    getters: {
        getUploadFlag(state){
            return state.fileData.uploadFlag
        }
    }
}