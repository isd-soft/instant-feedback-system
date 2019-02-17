import { Component, OnInit } from '@angular/core';
import { PresentationService } from '../services/presentation.service';
import { Presentation } from '../models/presentation.model';
import { Router, ActivatedRoute } from '@angular/router';
import { FormBuilder, FormControl } from '@angular/forms';
import { PresentationDTO } from '../models/dtos/presentation.dto';
import { NotifierService } from 'angular-notifier';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

  presentations: Presentation[];

  message: String;

  searchBox: FormControl = this.fb.control('');

  constructor(
    private ps: PresentationService,
    private router: Router,
    private route: ActivatedRoute,
    private fb: FormBuilder,
    private notifier: NotifierService
  ) { }

  ngOnInit() {
    this.route.data.subscribe((data: { presentations: Presentation[] }) => {
      this.presentations = data.presentations;
    });
  }

  requestAllPresentations() {
    this.ps.getPresentations().subscribe(presentationDtoList =>
      this.presentations = presentationDtoList
        .map(presentationDto => PresentationDTO.toModel(presentationDto)));
  }

  searchAll() {
    this.ps.getPresentationsByTitleOrEmailKeyword(this.searchBox.value)
      .subscribe(presentationDtoList => {
        if (presentationDtoList) {
        this.presentations = presentationDtoList
          .map(presentationDto => PresentationDTO.toModel(presentationDto));
        }
      }
    );
  }

  searchByEmail() {
    this.ps.getPresentationsByEmailKeyword(this.searchBox.value)
      .subscribe(presentationDtoList =>
        this.presentations = presentationDtoList
          .map(presentationDto => PresentationDTO.toModel(presentationDto)));
  }

  searchByTitle() {

    if (this.searchBox.invalid) {
      this.requestAllPresentations();
      return;
    }

    this.ps.getPresentationsByTitle(this.searchBox.value)
      .subscribe(presentationDtoList => this.presentations = presentationDtoList
        .map(presentationDto => PresentationDTO.toModel(presentationDto)));
  }

  openPresentationPage(i: number) {
    this.router.navigate([`/presentation-page/${this.presentations[i].id}`])
      .then().catch(() =>
      this.notifier.notify('error', 'Could not load presentation!'));
  }

}
