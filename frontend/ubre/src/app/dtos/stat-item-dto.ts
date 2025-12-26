export class StatItemDto {
  public label : string;
  public value : number;

  constructor(label : string, value : number) {
      this.label = label;
      this.value = value;
  }
}