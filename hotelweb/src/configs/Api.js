import axios from "axios";
import cookie from "react-cookies";

const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/SpringMVC_SMART_HOTEL/api";

/** Handler khi API trả 401 (token hết hạn/sai): clear token, logout, redirect về /login?next=... */
let authFailureHandler = null;
export function setAuthFailureHandler(handler) {
  authFailureHandler = handler;
}

export const endpoints = {
    'room-types': '/room-types',
    'rooms': '/rooms',
    'rooms-nearest-available': '/rooms/nearest-available',
    'services': '/services',
    'reservations': '/secure/reservations',
    'recommendations-me': '/secure/recommendations/me',
    'review': (id) => `/secure/reservations/${id}/review`,
    'payment-process': '/secure/process',
    'payment-status': '/status',
    'register': '/users',
    'login': '/login',
    'loginGoogle': '/login/google',
    'googleClientId': '/public/google-client-id',
    'profile': '/secure/profile',
    'customer-profile': '/secure/customer-profile',
    'loyalty': '/secure/loyalty',
    'chat': '/chat',
    'forgotPassword': '/auth/forgot-password',
    'resetPassword': '/auth/reset-password',
};

export const authApis = () => {
  const instance = axios.create({
    baseURL: BASE_URL,
    headers: {
      Authorization: `Bearer ${cookie.load("token")}`,
      "Content-Type": "application/json",
    },
  });

  instance.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401 && authFailureHandler) {
        authFailureHandler();
      }
      return Promise.reject(error);
    }
  );

  return instance;
};

export default axios.create({
    baseURL: BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});
