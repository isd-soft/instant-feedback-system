import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Presentation } from '../models/presentation.model';
import { environment } from 'src/environments/environment.prod';
import { PresentationDTO } from '../models/dtos/presentation.dto';

const API_URL = environment.apiUrl;
const API_MSG_ROUTE = environment.apiMessagesRoute;
const API_PRES_ROUTE = environment.apiPresentationsRoute;

@Injectable({
  providedIn: 'root'
})
export class PresentationService {

  constructor(private http: HttpClient) { }

  createPresentation(presentation: Presentation) {
    return this.http.post<PresentationDTO>(`${API_URL}${API_PRES_ROUTE}`, Presentation.toDTO(presentation));
  }

  getPresentations() {
    return this.http.get<PresentationDTO[]>(`${API_URL}${API_PRES_ROUTE}`);
  }

  getPresentationsByUser(userId: number) {
    return this.http.get<PresentationDTO[]>(`${API_URL}${API_PRES_ROUTE}`, {
      params: {
        userId: '' + userId
      }
    });
  }

  getPresentationsByTitle(title: string) {
    return this.http.get<PresentationDTO>(`${API_URL}${API_PRES_ROUTE}`, {
      params: {
        title: title
      }
    });
  }

  getPresentationById(id: number) {
    return this.http.get<PresentationDTO>(`${API_URL}${API_PRES_ROUTE}/${id}`);
  }

  updatePresentation(presentation: Presentation) {
    return this.http.put<PresentationDTO>(`${API_URL}${API_PRES_ROUTE}/${presentation.id}`, Presentation.toDTO(presentation));
  }

  deletePresentation(id: number) {
    return this.http.delete<PresentationDTO>(`${API_URL}${API_PRES_ROUTE}/${id}`);
  }

  deleteMessage(id: number) {
    return this.http.delete(`${API_URL}${API_MSG_ROUTE}/${id}`);
  }

  getAvgMark(presentation: Presentation) {

    let value = Number(0).toFixed(2);

    if (presentation.marks) {
      let sum = 0;
      presentation.marks.forEach(element => sum += element.mark);
      value = Number(sum / presentation.marks.length).toFixed(2);
    }

    return value;
  }
}
