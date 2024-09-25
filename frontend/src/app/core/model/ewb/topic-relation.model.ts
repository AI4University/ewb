export class TopicRelation {
	id: string;
	correlations: TopicCorrelation[];
}

export class TopicCorrelation {
	id: string;
	score: number;
}
