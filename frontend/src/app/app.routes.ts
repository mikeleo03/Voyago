import { Routes } from '@angular/router';
import { RouterConfig } from './config/app.constants';
// import { AuthGuard } from './main/guards/auth-guard.service';

export const routes: Routes = [
    {
        path: RouterConfig['LOGIN'].path,
        loadChildren: () =>
            import('./pages/login/login.routes').then((m) => m.loginRoutes),
        title: RouterConfig['LOGIN'].title,
        data: RouterConfig['LOGIN'].data,
    },
    {
        path: RouterConfig['SIGNUP'].path,
        loadChildren: () =>
            import('./pages/signup/signup.routes').then((m) => m.signupRoutes),
        title: RouterConfig['SIGNUP'].title,
        data: RouterConfig['SIGNUP'].data,
    },
    {
        path: RouterConfig['CHANGES'].path,
        loadChildren: () =>
            import('./pages/change/change.routes').then((m) => m.changeRoutes),
        title: RouterConfig['CHANGES'].title,
        data: RouterConfig['CHANGES'].data,
    },
    {
        path: '',
        // canActivate: [AuthGuard],
        children: [
            {
                path: RouterConfig['HOME'].path,
                loadChildren: () =>
                    import('./pages/home/home.routes').then((m) => m.homeRoutes),
                title: RouterConfig['HOME'].title,
                data: RouterConfig['HOME'].data,
            }
        ],
    },
    {
        path: RouterConfig['DASHBOARD'].path,
        loadChildren: () =>
            import('./pages/dashboard/dashboard.routes').then((m) => m.dashboardRoutes),
        title: RouterConfig['DASHBOARD'].title,
        data: RouterConfig['DASHBOARD'].data,
    },
    {
        path: RouterConfig['BOOK_TICKET'].path,
        loadChildren: () =>
            import('./pages/create-ticket/create-ticket.routes').then((m) => m.createTicketRoutes),
        title: RouterConfig['BOOK_TICKET'].title,
        data: RouterConfig['BOOK_TICKET'].data,
    },
    {
        path: RouterConfig['TOURS'].path,
        loadChildren: () =>
            import('./pages/tours/tours.routes').then((m) => m.toursRoutes),
        title: RouterConfig['TOURS'].title,
        data: RouterConfig['TOURS'].data,
    },
    {
        path: RouterConfig['TOURS_ADMIN'].path,
        loadChildren: () =>
            import('./pages/admin/tours-admin/tours-admin.routes').then((m) => m.toursAdminRoutes),
        title: RouterConfig['TOURS_ADMIN'].title,
        data: RouterConfig['TOURS_ADMIN'].data,
    },
    {
        path: RouterConfig['TOUR_DETAIL'].path,
        loadChildren: () =>
            import('./pages/tour-detail/tour-detail.routes').then((m) => m.tourDetailRoutes),
        title: RouterConfig['TOUR_DETAIL'].title,
        data: RouterConfig['TOUR_DETAIL'].data,
    },
    {
        path: RouterConfig['TOUR_DETAIL_ADMIN'].path,
        loadChildren: () =>
            import('./pages/admin/tour-detail-admin/tour-detail-admin.routes').then((m) => m.tourDetailAdminRoutes),
        title: RouterConfig['TOUR_DETAIL_ADMIN'].title,
        data: RouterConfig['TOUR_DETAIL_ADMIN'].data,
    },
    {
        path: RouterConfig['CUSTOMERS_ADMIN'].path,
        loadChildren: () =>
            import('./pages/admin/customers-admin/customers-admin.routes').then((m) => m.customersAdminRoutes),
        title: RouterConfig['CUSTOMERS_ADMIN'].title,
        data: RouterConfig['CUSTOMERS_ADMIN'].data,
    },
    {
        path: RouterConfig['HISTORY'].path,
        loadChildren: () =>
            import('./pages/history/history.routes').then((m) => m.historyRoutes),
        title: RouterConfig['HISTORY'].title,
        data: RouterConfig['HISTORY'].data,
    },
    {
        path: RouterConfig['TICKETS_ADMIN'].path,
        loadChildren: () =>
            import('./pages/admin/tickets-admin/tickets-admin.routes').then((m) => m.ticketsAdminRoutes),
        title: RouterConfig['TICKETS_ADMIN'].title,
        data: RouterConfig['TICKETS_ADMIN'].data,
    },
    {
        path: RouterConfig['TICKET_DETAIL_ADMIN'].path,
        loadChildren: () =>
            import('./pages/admin/ticket-detail-admin/ticket-detail-admin.routes').then((m) => m.ticketDetailAdminRoutes),
        title: RouterConfig['TICKET_DETAIL_ADMIN'].title,
        data: RouterConfig['TICKET_DETAIL_ADMIN'].data,
    },
    {
        path: RouterConfig['NOT_FOUND'].path,
        loadChildren: () =>
            import('./pages/not-found/not-found.routes').then((m) => m.notFoundRoutes),
        title: RouterConfig['NOT_FOUND'].title,
        data: RouterConfig['NOT_FOUND'].data,
    }
];