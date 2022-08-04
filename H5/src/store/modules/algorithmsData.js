export default {
    namespaced:true,
    actions:{
        //添加用户设置的算法数据
        addAlgorithmsData(context,val){
            let isHav = false
            if(context.state.algorithmsData.length){
                context.state.algorithmsData.forEach(function(algorithm){
                    if(algorithm["id"] === val["id"]){
                        isHav = true
                        context.commit('UPDATE_ALGORITHMS_DATA',val)
                    }
                })
            }
            if(!isHav){
                context.commit('ADD_ALGORITHMS_DATA',val)
            }
        },
        //删除一个算法参数
        deleteAlgorithmsData(context,id){
            context.commit('DELETE_ALGORITHMS_DATA',id)
        },
        //删除所有算法参数
        deleteAllAlgorithmsData(context){
            context.commit('DELETE_ALL_ALGORITHMS_DATA')
        }
    },
    mutations:{
        //添加用户设置的算法数据
        ADD_ALGORITHMS_DATA(state,val){
            state.algorithmsData.push(val)
        },
        //修改参数
        UPDATE_ALGORITHMS_DATA(state,val){
            let algorithmsData = state.algorithmsData
            algorithmsData.forEach(function(algorithm){
                if(algorithm["id"] === val["id"]){
                    //设置过该算法的参数则进行替换
                    algorithm["parameters"]= val["parameters"]
                }
            })
            state.algorithmsData = algorithmsData
        },
        //删除一个算法参数
        DELETE_ALGORITHMS_DATA(state,id){
            let algorithmsData = state.algorithmsData 
            if(algorithmsData.length){
                state.algorithmsData = algorithmsData.filter(algorithm => algorithm["id"] !== id)
            }
        },
        //删除所有算法参数
        DELETE_ALL_ALGORITHMS_DATA(state){
            
            state.algorithmsData=[]
        }
    },
    state:{
        //算法参数-提交数据用
        algorithmsData:[]
    },
    getters:{
        getAlgorithmsData(state){
            return state.algorithmsData
        }
    }
}