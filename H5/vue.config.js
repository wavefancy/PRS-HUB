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
        target: "http://12.12.1.55:8081/prs/hub/",
        pathRewrite: { '^/prd-api': '' },
      },
    },
  },
})
