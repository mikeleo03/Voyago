import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment.prod';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Ticket, TicketDTO, TicketSaveDTO } from '../../models/ticket.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = `${environment.apiUrl}/ticket`;

  constructor(private http: HttpClient) { }

  // Create ticket
  createTicket(ticketSaveDTO: TicketSaveDTO): Observable<TicketDTO> {
    return this.http.post<TicketDTO>(this.apiUrl, ticketSaveDTO);
  }

  getAllTickets(
    minPrice?: number,
    maxPrice?: number,
    sortPrice?: string,
    sortStatus?: string,
    startDate?: string,
    endDate?: string,
    page: number = 0,
    size: number = 10
  ): Observable<{ tickets: Ticket[], currentPage: number, totalItems: number, totalPages: number }> {
    let params = new HttpParams();
    if (minPrice !== undefined) params = params.append('minPrice', minPrice.toString());
    if (maxPrice !== undefined) params = params.append('maxPrice', maxPrice.toString());
    if (sortPrice) params = params.append('sortPrice', sortPrice);
    if (sortStatus) params = params.append('sortStatus', sortStatus);
    if (startDate) params = params.append('startDate', startDate);
    if (endDate) params = params.append('endDate', endDate);
    params = params.append('page', page.toString());
    params = params.append('size', size.toString());

    return this.http.get<{ tickets: Ticket[], currentPage: number, totalItems: number, totalPages: number }>(this.apiUrl, { params });
  }

  getAllTicketsByUserID(
    minPrice?: number,
    maxPrice?: number,
    startDate?: string,
    endDate?: string,
    page: number = 0,
    size: number = 10
  ): Observable<{ tickets: Ticket[], currentPage: number, totalItems: number, totalPages: number }> {
    let params = new HttpParams();
    if (minPrice !== undefined) params = params.append('minPrice', minPrice.toString());
    if (maxPrice !== undefined) params = params.append('maxPrice', maxPrice.toString());
    if (startDate) params = params.append('startDate', startDate);
    if (endDate) params = params.append('endDate', endDate);
    params = params.append('page', page.toString());
    params = params.append('size', size.toString());

    return this.http.get<{ tickets: Ticket[], currentPage: number, totalItems: number, totalPages: number }>(`${this.apiUrl}/list`, { params });
  }

  getTicketById(id: string): Observable<TicketDTO> {
    return this.http.get<TicketDTO>(`${this.apiUrl}/${id}`);
  }

  updateTicketStatus(id: string, newStatus: string): Observable<TicketDTO> {
    return this.http.put<TicketDTO>(`${this.apiUrl}/${id}/status`, { status: newStatus });
  }

  exportTicketsToExcel(tickets: Ticket[]): Observable<Blob> {
    return this.http.post(`${this.apiUrl}/excel`, { tickets }, { responseType: 'blob' });
  }

  reduceTourQuota(paymentId: string): Observable<void> {
    const params = new HttpParams().set('paymentId', paymentId);
    return this.http.put<void>(`${this.apiUrl}/return`, null, { params });
  }
}
