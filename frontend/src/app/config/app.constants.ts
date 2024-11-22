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
    DASHBOARD: { path: 'dashboard', link: '/dashboard', title: 'Dashboard', data: { header: true } },
    CHANGES: { path: 'change', link: '/change', title: 'Change Password', data: { header: true } },
    TOURS: { path: 'tours', link: '/tours', title: 'Tours', data: { header: true } },
    TOURS_ADMIN: { path: 'admin/tours', link: '/admin/tours', title: 'Tours', data: { header: true } },
    TOUR_DETAIL: { path: 'tour', link: '/tour', title: 'Tour Detail', data: { header: true } },
    TOUR_DETAIL_ADMIN: { path: 'admin/tour', link: '/admin/tour', title: 'Tour Detail', data: { header: true } },
    HISTORY: { path: 'history', link: '/history', title: 'History', data: { header: true } },
    TICKETS_ADMIN: { path: 'admin/tickets', link: '/admin/tickets', title: 'Tickets', data: { header: true } },
    TICKET_DETAIL: { path: 'ticket', link: '/ticket', title: 'Ticket Detail', data: { header: true } },
    TICKET_DETAIL_ADMIN: { path: 'admin/ticket', link: '/admin/ticket', title: 'Ticket Detail', data: { header: true } },
    CUSTOMERS_ADMIN: { path: 'admin/customers', link: '/admin/customers', title: 'Customers', data: { header: true } },
    BOOK_TICKET: { path: 'ticket/create', link: '/ticket/create', title: 'Booking Ticket', data: { header: true } },
    NOT_FOUND: { path: '**', link: '', title: 'Page Not Found', data: { header: true } },
};