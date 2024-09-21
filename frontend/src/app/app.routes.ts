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
        path: RouterConfig['NOT_FOUND'].path,
        loadChildren: () =>
            import('./pages/not-found/not-found.routes').then((m) => m.notFoundRoutes),
        title: RouterConfig['NOT_FOUND'].title,
        data: RouterConfig['NOT_FOUND'].data,
    },
];
