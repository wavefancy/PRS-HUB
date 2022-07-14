// 该文件专门用于创建整个应用的路由器
//引入路由插件
import VueRouter from "vue-router";

//引入组件
import AnalysisItem from '@/pages/integrations/AnalysisItem'
import MultipleAncestriesItem from '@/pages/integrations/MultipleAncestriesItem'
import StatisticsItem from '@/pages/task/StatisticsItem'
import ReferenceItem from '@/pages/integrations/ReferenceItem'
import LoginItem from '@/pages/authentication/LoginItem'
import RegisterItem from '@/pages/authentication/RegisterItem'
import SettingsItem from '@/pages/authentication/SettingsItem'
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
                    name:"gwasreference",
                    path:'/gwasreference',
                    component:ReferenceItem,
                    props(){
                        return {
                            pageTitle:'GWAS Summary Statistics',
                            type:'GWAS'
                        }
                    }
                }
                ,
                {
                    name:"ldreference",
                    path:'/ldreference',
                    component:ReferenceItem,
                    props(){
                        return {
                            pageTitle:'LD References',
                            type:'LD'
                        }
                    }
                },
                {
                    name:"analysis",
                    path:'/analysis',
                    component:AnalysisItem
                },
                {
                    name:"multipleAncestries",
                    path:'/multipleAncestries',
                    component:MultipleAncestriesItem
                },
                {
                    name:"statistics",
                    path:'/statistics',
                    component:StatisticsItem
                },
                {
                    name:"settings",
                    path:'/settings',
                    component:SettingsItem
                },
                
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