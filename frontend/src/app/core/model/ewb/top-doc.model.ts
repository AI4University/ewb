export class TopDoc {
	id: string;
	title: string;
	topic: number;
	words: number;
	relevance: number;
	counts: Map<string, number>;
	token: number;
}
