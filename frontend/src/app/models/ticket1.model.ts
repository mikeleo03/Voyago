export interface TicketDetailDTO {
    name: string;
    phone: string;
}

export interface TicketDTO {
    username: string;
    tourID: string;
    paymentID: string;
    status: string;
    startDate: string;
    endDate: string;
    price: number;
    isRescheduled: boolean;
    isReviewed: boolean;
    ticketDetails: TicketDetailDTO[];
}
