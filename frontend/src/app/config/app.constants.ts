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
    TOURS: { path: 'tours', link: '/tours', title: 'Tours', data: { header: true } },
    TOUR_DETAIL: { path: 'tour', link: '/tour', title: 'Tour Detail', data: { header: true } },
    NOT_FOUND: { path: '**', link: '', title: 'Page Not Found', data: { header: true } },
};