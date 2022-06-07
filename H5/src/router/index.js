// 该文件专门用于创建整个应用的路由器
//引入路由插件
import VueRouter from "vue-router";

//引入组件
import AnalysisItem from '@/pages/integrations/AnalysisItem'
import StatisticsItem from '@/pages/task/StatisticsItem'
import ReferenceItem from '@/pages/integrations/ReferenceItem'
import LoginItem from '@/pages/authentication/LoginItem'
import RegisterItem from '@/pages/authentication/RegisterItem'
import HomeItem from '@/pages/home'
import FunctionItem from '@/pages/function'
import ResultItem from '@/pages/authentication/ResultItem'
export default new VueRouter({
    routes:[
        {
            path: '/',
            redirect:'/home'
          },
        {
            name:'home',
            path:'/home',
            component:HomeItem,
        },
        {
            name:'functionItem',
            path:'/functionItem',
            component:FunctionItem,
            //重定向
            redirect: '/analysis',
            children: [
                {
                    name:"analysis",
                    path:'/analysis',
                    component:AnalysisItem
                },
                {
                    name:"statistics",
                    path:'/statistics',
                    component:StatisticsItem
                },
                {
                    name:"reference",
                    path:'/reference',
                    component:ReferenceItem
                }
            ],
        },
        {
            name:"login",
            path:'/login',
            component:LoginItem
        },
        {
            name:"register",
            path:'/register',
            component:RegisterItem
        },
        {
            name:"result",
            path:'/result',
            component:ResultItem
        }
    ]
})