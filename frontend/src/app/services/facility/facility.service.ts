import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment.prod';
import { Facility, FacilityDTO } from '../../models/facility.model';

@Injectable({
  providedIn: 'root'
})
export class FacilityService {
  private apiUrl = `${environment.apiUrl}/facility`;

  constructor(private http: HttpClient) { }

  createFacility(facility: FacilityDTO): Observable<FacilityDTO> {
    return this.http.post<FacilityDTO>(this.apiUrl, facility);
  }

  updateFacility(id: string, facility: FacilityDTO): Observable<FacilityDTO> {
    return this.http.put<FacilityDTO>(`${this.apiUrl}/${id}`, facility);
  }

  deleteFacility(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getFacilitiesByTourId(tourId: string): Observable<FacilityDTO[]> {
    return this.http.get<FacilityDTO[]>(`${this.apiUrl}/tour/${tourId}`);
  }
}
