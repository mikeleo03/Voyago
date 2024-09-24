import { Injectable } from '@angular/core';
import { environment } from '../../../environment/environment.prod';
import { HttpClient } from '@angular/common/http';
import { TicketDTO, TicketSaveDTO } from '../../models/ticket.model';
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
}
