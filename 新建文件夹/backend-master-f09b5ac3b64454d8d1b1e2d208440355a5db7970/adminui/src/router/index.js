import Vue from 'vue';
import Router from 'vue-router';
const _import = require('./_import_' + process.env.NODE_ENV);
// in development env not use Lazy Loading,because Lazy Loading large page will cause webpack hot update too slow.so only in production use Lazy Loading

/* layout */
import Layout from '../views/layout/Layout';

/* login */
const Login = _import('login/index');

/* dashboard */
const dashboard = _import('dashboard/index');

/* error page */
const Err404 = _import('404');

/* */
const deviceMap = _import('device/map');
const deviceList = _import('device/list');

const customIndex = _import('custom/index');

const messageIndex = _import('message/index');

Vue.use(Router);

 /**
  * icon : the icon show in the sidebar
  * hidden : if `hidden:true` will not show in the sidebar
  * redirect : if `redirect:noredirect` will not redirct in the levelbar
  * noDropdown : if `noDropdown:true` will not has submenu in the sidebar
  * meta : `{ role: ['admin'] }`  will control the page role
  **/
export const constantRouterMap = [
  { path: '/login', component: Login, hidden: true },
  { path: '/404', component: Err404, hidden: true },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    name: 'Home',
    hidden: true,
    children: [{ path: 'dashboard', component: dashboard }]
  }
]

export default new Router({
  // mode: 'history', //后端支持可开
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRouterMap
});

export const asyncRouterMap = [
  {
    path: '/device',
    component: Layout,
    redirect: '/device/map',
    name: '设备管理',
    icon: 'shebei',
    meta: { role: ['device'] },
    children: [
      { path: 'map', component: deviceMap, name: '设备地图', meta: { role: ['device:map'] } },
      { path: 'list', component: deviceList, name: '设备列表', meta: { role: ['device:list', 'device:edit', 'device:at'] } }
    ]
  },

  {
    path: '/custom',
    component: Layout,
    redirect: 'noredirect',
    name: '账号管理',
    icon: 'customers',
    noDropdown: true,
    meta: { role: ['custom'] },
    children: [
        { path: 'index', component: customIndex, name: '账号管理', meta: { role: ['custom:list', 'custom:edit'] } }
    ]
  },

  {
    path: '/setting',
    component: Layout,
    redirect: 'noredirect',
    name: '系统设置',
    icon: 'auth',
    meta: { role: ['sysconfig'] },
    children: [
      { path: 'device_type', name: '行业代码表', component: _import('setting/device_type'), meta: { role: ['sysconfig:dtype'] } },
      { path: 'manufacturer', name: '生产厂家表', component: _import('setting/manufacturer'), meta: { role: ['sysconfig:manufacturer'] } },
      { path: 'area_code', name: '地域码表', component: _import('setting/area_code'), meta: { role: ['sysconfig:areacode'] } },
      { path: 'device_setting', name: '参数设置', component: _import('device/setting'), meta: { role: ['sysconfig:paramset'] } }
    ]
  },

  {
    path: '/auth',
    component: Layout,
    redirect: 'noredirect',
    name: '权限管理',
    icon: 'auth',
    meta: { role: ['auth_role','auth_user'] },
    children: [
      { path: 'role', component: _import('auth/role'), name: '角色管理', meta: { role: ['auth_role:list', 'auth_role:edit'] } },
      { path: 'user', component: _import('auth/user'), name: '奥特管理员', meta: { role: ['auth_user:list', 'auth_user:edit'] } }
    ]
  },

  {
    path: '/message',
    component: Layout,
    redirect: 'noredirect',
    name: '预警管理',
    icon: 'notify1',
    noDropdown: true,
    meta: { role: ['warn'] },
    children: [
        { path: 'index', component: messageIndex, name: '预警管理', meta: { role: ['warn:list'] } }
    ]
  },

  {
    path: '/setting',
    component: Layout,
    redirect: '/setting/index',
    name: '账户设置',
    icon: 'setting',
    noDropdown: true,
    children: [
        { path: 'index', name: '账户设置', component: _import('setting/index'), meta: { role: ['default'] } }
    ]
  },

  { path: '*', redirect: '/404', hidden: true }
];
