import fetch from '@/utils/fetch';

export function getCustomList(params) {
  return fetch({
    url: '/custom/list',
    method: 'post',
    data: params
  });
}

export function saveCustom(params) {
  return fetch({
    url: '/custom/save',
    method: 'post',
    data: params
  });
}

export function changeCustomStatus(params) {
  return fetch({
    url: '/custom/change_status',
    method: 'post',
    data: params
  });
}
