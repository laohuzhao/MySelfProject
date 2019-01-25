import fetch from '@/utils/fetch';

export function login(username, password) {
  return fetch({
    url: '/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  });
}

export function getInfo(token) {
  return fetch({
    url: '/user/info',
    method: 'get',
    params: { token }
  });
}

export function logout() {
  return fetch({
    url: '/user/logout',
    method: 'get'
  });
}

export function getHomeInfo(params) {
  return fetch({
    url: '/home/info',
    method: 'post',
    data: params
  });
}

