const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  
  devServer: {
    port: 3366,
    allowedHosts: [
      'localhost',
      '.cpolar.top'
    ],
    proxy: {
      '/ai': {
        target: 'http://192.168.100.128',
        changeOrigin: true,
      }
    }
  }
})