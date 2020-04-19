import Vue from 'vue'
import Router from 'vue-router'
import Index from '@/page/index'
import Home from '@/page/home/Home'
import BatchQueryDrivers from '@/page/query/BatchQueryDrivers'
import BatchQueryCars from '@/page/query/BatchQueryCars'
import ImportCarList from '@/page/query/ImportCarList'
import ImportDriverList from '@/page/query/ImportDriverList'

const NotFound = () => import('@/page/404/NotFound.vue')

Vue.use(Router)
var router = new Router({
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/page/login/Login')
    },
    {
      path: '/',
      component: Index,
      children: [
        {
          path: "/",
          name: Home.name,
          component: Home,
          meta: {
            primary: true
          }
        },
        {
          path: "/importDriver",
          name: ImportDriverList.name,
          component: ImportDriverList,
          meta: {
            primary: true,
            type:"driver"
          }
        },
        {
          path: "/importCar",
          name: ImportCarList.name,
          component: ImportCarList,
          meta: {
            primary: true,
            type:"car"
          }
        },
        {
          path: "/batchQueryDrivers",
          name: BatchQueryDrivers.name,
          component: BatchQueryDrivers,
          meta: {
            primary: false,
            parent: BatchQueryDrivers.name
          }
        },
        {
          path: "/batchQueryCars",
          name: BatchQueryCars.name,
          component: BatchQueryCars,
          meta: {
            primary: false,
            parent: ImportCarList.name
          }
        }
      ]
    }
    ,
    {path: '*', component: NotFound}
  ]
});
router.findParent = (path) => {
  let menu = findRouter(router.options.routes, path);
  return !menu || menu.name
}

function findRouter(items, path) {
  var result = null;
  for (var index in items) {
    let item = items[index];
    if (item.meta && item.meta.primary && item.path!==""  && path.startsWith(item.path)) {
      result = item
      break;
    }
    if (item.children && item.children.length > 0) {
      result = findRouter(item.children, path);
      if(result) {
        return result;
      }
    }
  }
  return result;
}

router.beforeEach((to, from, next) => {
  if (to.path === '/login') {
    sessionStorage.removeItem('token');
  }
  var user = sessionStorage.getItem('token');
  if (!user && to.path !== '/login') {
    next({
      path: '/login'
    })
  } else {
    next();
  }
});

/**
 * 路由重复点击，忽略错误
 * @type {Router.push|*}
 */
const originalPush = Router.prototype.push
Router.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}
export default router
