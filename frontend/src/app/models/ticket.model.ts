// Main Model
export interface Ticket {
    id: string;
    username: string;
    tourID: string;
    paymentID: string;
    status: string;
    startDate: number[];
    endDate: number[];
    price: number;
    isRescheduled: boolean;
    isReviewed: boolean;
    ticketDetail: TicketDetail[];
}

export interface TicketDetailKey {
    id: string;
    ticketId: string;
}

export interface TicketDetail {
    id: TicketDetailKey;
    ticket: Ticket;
    name: string;
    phone: string;
}

// DTOs
export interface TicketDTO {
    username: string;
    tourID: string;
    paymentID: string;
    status: string;
    startDate: Date;
    endDate: Date;
    price: number;
    isRescheduled: boolean;
    isReviewed: boolean;
    ticketDetails: TicketDetailDTO[];
}

export interface TicketDetailDTO {
    name: string;
    phone: string;
}

export interface TicketSaveDTO {
    username: string;
    tourID: string;
    startDate: Date;
    endDate: Date;
    ticketDetails: TicketDetailDTO[];
}