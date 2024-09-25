import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { EwbService } from '@app/core/services/http/ewb.service';
import { BaseComponent } from '@common/base/base.component';
import { QueryResult } from '@common/model/query-result';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-taxonomy-select',
  templateUrl: './taxonomy-select.component.html',
  styleUrls: ['./taxonomy-select.component.scss']
})
export class TaxonomySelectComponent extends BaseComponent implements OnInit {

  taxonomyList: string[] = [];
  selectedTaxonomy: string = null;
  @Output() valueSelected: EventEmitter<{ taxonomy: string }> = new EventEmitter<{ taxonomy: string }>();

  constructor(private ewbService: EwbService) {
    super();
  }

  ngOnInit(): void {
    this.ewbService.list_avail_taxonomies()
      .pipe(takeUntil(this._destroyed))
      .subscribe((queryResult: QueryResult<string>) => {
        this.taxonomyList = queryResult.items;
      });
  }

  valueChanged(_event: any): void {
    this.valueSelected.emit({ taxonomy: this.selectedTaxonomy });
  }

}
