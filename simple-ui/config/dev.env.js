'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',
  BASE_API: '"tools"',
  SYSTEM_NAME: '"网约车司机查询系统"'
})
