export const AppConstants = {
    APPLICATION_NAME: 'Voyago',
    BASE_API_URL: 'http://localhost:8080/api/v1',
};

export interface RouteLink {
    path: string;
    link?: string;
    title: string;
    data?: any;
}

export const RouterConfig: Record<string, RouteLink> = {
    HOME: { path: '', link: '/', title: 'Home', data: { header: true } },
    LOGIN: { path: 'login', link: '/login', title: 'Login', data: { header: true } },
    SIGNUP: { path: 'signup', link: '/signup', title: 'Signup', data: { header: true } },
    NOT_FOUND: { path: '**', link: '', title: 'Page Not Found', data: { header: true } },
};