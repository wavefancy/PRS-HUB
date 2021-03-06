const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave:false ,// 关闭语法检查
  //配置代理跨域
  devServer: {
    proxy: {
      "/api": {
        target: "http://localhost:9091/prs/hub/",
        pathRewrite: { '^/api': '' },
      },
      "/prd-api": {
        target: "http://192.168.118.93:9091/prs/hub/",
        pathRewrite: { '^/prd-api': '' },
      },
    },
  },
})
