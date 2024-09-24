import { Routes } from '@angular/router';
import { CreateTicketSetupComponent } from './create-ticket-setup/create-ticket-setup.component';
import { CreateTicketPaymentComponent } from './create-ticket-payment/create-ticket-payment.component';

export const createTicketRoutes: Routes = [
    { path: 'setup/:id', component: CreateTicketSetupComponent },
    { path: 'payment/:id', component: CreateTicketPaymentComponent },
];