import { computed, Injectable, signal } from "@angular/core";
import { BehaviorSubject, Observable } from "rxjs";

@Injectable({
    providedIn:'root'
})
export class SideNavService {

    private _sideNavSignal = signal<boolean>(true);

    public isOpen = computed(() => this._sideNavSignal());
    public toggle(){
        this._sideNavSignal.update((val) => !val);
    }

}
