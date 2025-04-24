export class TopDoc {
	id: string;
	name: string;
	topic: number;
	words: number;
	relevance: number;
	counts: Map<string, number>;
	token: number;
}
